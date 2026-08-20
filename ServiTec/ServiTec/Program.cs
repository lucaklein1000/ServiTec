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
using ServiTec.Services;
using System.Text;
using System.Text.Json.Serialization;

var builder = WebApplication.CreateBuilder(args);

// Configurar el servidor per escoltar en HTTP (5206) i HTTPS (7123)
builder.WebHost.UseUrls("http://0.0.0.0:5206", "https://0.0.0.0:7123");

// Configuració de la connexió amb la base de dades SQL Server
builder.Services.AddDbContext<ServiTecDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("connectionDB"))
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
                    "http://10.0.2.2:5206",      // Emulador d'Android (HTTP)
                    "https://10.0.2.2:7123",     // Emulador d'Android (HTTPS)
                    "http://localhost:5206",      // Accés local directe HTTP
                    "https://localhost:7123",     // Accés local directe HTTPS
                    "http://localhost:5173",      // Front-End Web (Vite / React / Vue)
                    "http://localhost:4200"       // Front-End Web (Angular)
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
    options.RequireHttpsMetadata = true; // Forçar metadades sobre HTTPS en entorns de producció
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

// Configurar el pipeline de peticions HTTP
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseRouting();

// CORS s'ha de col·locar estrictament entre UseRouting i UseAuthentication
app.UseCors(allowServiTecOrigins);

// Redirecció automàtica de tot el tràfic HTTP cap a HTTPS
app.UseHttpsRedirection();

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