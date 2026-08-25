// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Program.cs
// Descripció:    Punt d'entrada principal de l'aplicació ASP.NET Core. Configura
//                el contenidor d'injecció de dependències, la base de dades,
//                la seguretat JWT, la política CORS restrictiva, HTTPS i Swagger.
// ============================================================================

using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using ServiTec.Application.Interfaces;
using ServiTec.Application.Services;
using ServiTec.Infrastructure.Data;
using ServiTec.Infrastructure.Repositories;
using System.Text;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

// Configuració de la connexió amb la base de dades SQL Server
builder.Services.AddDbContext<ServiTecDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection"))
);

// Afegir controladors i configuració de serialització JSON per evitar referències circulars
builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;
    });

// Injecció de dependències dels serveis d'aplicació i repositoris
builder.Services.AddScoped<ProducteService>();
builder.Services.AddScoped<UsuariService>();
builder.Services.AddScoped<TaulaService>();
builder.Services.AddScoped<CategoriaService>();
builder.Services.AddScoped<ComandaService>();
builder.Services.AddScoped<MenjadorService>();
builder.Services.AddScoped<IJwtService, JwtService>();
builder.Services.AddScoped<AuthService>();
builder.Services.AddScoped(typeof(IRepository<>), typeof(Repository<>));

// Configuració de Swagger amb suport per a Bearer Token (JWT)
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "ServiTec API",
        Version = "v1"
    });

    var securityScheme = new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Description = "Introdueix el token JWT generat al login. Exemple: eyJhbGciOiJIUzI1NiI...",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        Reference = new OpenApiReference
        {
            Type = ReferenceType.SecurityScheme,
            Id = "Bearer"
        }
    };

    options.AddSecurityDefinition("Bearer", securityScheme);

    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        { securityScheme, new string[] { } }
    });
});

// Configuració de la política CORS segura i compatible amb el Front-End i l'App Android
var allowServiTecOrigins = "_allowServiTecOrigins";
builder.Services.AddCors(options =>
{
    options.AddPolicy(allowServiTecOrigins, policy =>
    {
        policy.WithOrigins(
                    "http://10.0.2.2:5206",         // Emulador de Android (HTTP)
                    "https://10.0.2.2:7123",        // Emulador de Android (HTTPS)
                    "http://localhost:5206",         // Acceso local directo HTTP
                    "https://localhost:7123",        // Acceso local directo HTTPS
                    "http://10.45.94.221:5206",      // Tu IP local (HTTP)
                    "https://10.45.94.221:7123",     // Tu IP local (HTTPS)
                    "http://localhost:5173",         // Front-End Web
                    "http://localhost:4200"          // Front-End Web
              )
              .SetIsOriginAllowedToAllowWildcardSubdomains()
              .AllowAnyHeader()
              .AllowAnyMethod();
    });
});

// Configuració dels paràmetres de validació del token JWT
var jwtSettings = builder.Configuration.GetSection("Jwt");
var key = Encoding.UTF8.GetBytes(jwtSettings["Key"]!);

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.RequireHttpsMetadata = false; // Permet terminació TLS a Azure
    options.SaveToken = true;
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(key),
        ValidateIssuer = true,
        ValidIssuer = jwtSettings["Issuer"],
        ValidateAudience = true,
        ValidAudience = jwtSettings["Audience"],
        ValidateLifetime = true,
        ClockSkew = TimeSpan.Zero
    };
});

builder.Services.AddAuthorization();

var app = builder.Build();

// Enable Swagger per a tots els entorns (desenvolupament i producció a Azure)
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "ServiTec API v1");
    c.RoutePrefix = "swagger";
});

app.UseRouting();

// CORS s'ha de col·locar strictly entre UseRouting i UseAuthentication
app.UseCors(allowServiTecOrigins);

// SOLO REDIRIGIR A HTTPS SI NO ESTAMOS EN DESARROLLO LOCAL
if (!app.Environment.IsDevelopment())
{
    app.UseHttpsRedirection();
}

app.UseAuthentication();
app.UseAuthorization();

// Redirecció automàtica de l'arrel de l'API cap a la interfície de Swagger
app.Use(async (context, next) =>
{
    if (context.Request.Path == "/")
    {
        context.Response.Redirect("/swagger/index.html", permanent: false);
        return;
    }
    await next();
});

app.MapControllers();

app.Run();