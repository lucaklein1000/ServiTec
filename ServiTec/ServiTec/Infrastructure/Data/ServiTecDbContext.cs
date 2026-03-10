using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using ServiTec.Domain.Models;

namespace ServiTec.Infrastructure.Data;

public partial class ServiTecDbContext : DbContext
{
    public ServiTecDbContext()
    {
    }

    public ServiTecDbContext(DbContextOptions<ServiTecDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Categoria> Categoria { get; set; }

    public virtual DbSet<Comanda> Comanda { get; set; }

    public virtual DbSet<LiniaComanda> LiniaComanda { get; set; }

    public virtual DbSet<LiniaUsuari> LiniaUsuaris { get; set; }

    public virtual DbSet<Producte> Productes { get; set; }

    public virtual DbSet<Taula> Taules { get; set; }

    public virtual DbSet<Usuari> Usuaris { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Categoria>(entity =>
        {
            entity.HasKey(e => e.IdCategoria).HasName("PK__Categori__8A3D240C8C15AA44");

            entity.Property(e => e.IdCategoria).HasColumnName("idCategoria");
            entity.Property(e => e.Descripcio)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("descripcio");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasColumnName("nom");
        });

        modelBuilder.Entity<Comanda>(entity =>
        {
            entity.HasKey(e => e.IdComanda).HasName("PK__Comanda__2C0FFB318F8E757E");

            entity.Property(e => e.IdComanda).HasColumnName("idComanda");
            entity.Property(e => e.DataCreacio)
                .HasDefaultValueSql("(getdate())")
                .HasColumnType("datetime")
                .HasColumnName("dataCreacio");
            entity.Property(e => e.Estat)
                .HasMaxLength(20)
                .IsUnicode(false)
                .HasColumnName("estat");
            entity.Property(e => e.IdTaula).HasColumnName("idTaula");
            entity.Property(e => e.IdUsuari).HasColumnName("idUsuari");
            entity.Property(e => e.Total)
                .HasColumnType("decimal(10, 2)")
                .HasColumnName("total");

            entity.HasOne(d => d.IdTaulaNavigation).WithMany(p => p.Comanda)
                .HasForeignKey(d => d.IdTaula)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Comanda_Taula");

            entity.HasOne(d => d.IdUsuariNavigation).WithMany(p => p.Comanda)
                .HasForeignKey(d => d.IdUsuari)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Comanda_Usuari");
        });

        modelBuilder.Entity<LiniaComanda>(entity =>
        {
            entity.HasKey(e => e.IdLinia).HasName("PK__LiniaCom__4F216B384DB3C22A");

            entity.Property(e => e.IdLinia).HasColumnName("idLinia");
            entity.Property(e => e.IdComanda).HasColumnName("idComanda");
            entity.Property(e => e.IdProducte).HasColumnName("idProducte");
            entity.Property(e => e.PreuUnitari)
                .HasColumnType("decimal(10, 2)")
                .HasColumnName("preuUnitari");
            entity.Property(e => e.Quantitat).HasColumnName("quantitat");
            entity.Property(e => e.Subtotal)
                .HasColumnType("decimal(10, 2)")
                .HasColumnName("subtotal");


            entity.HasOne(d => d.IdComandaNavigation).WithMany(p => p.LiniaComanda)
                .HasForeignKey(d => d.IdComanda)
                .HasConstraintName("FK_LiniaComanda_Comanda");

            /*entity.HasOne(d => d.IdProducteNavigation).WithMany(p => p.LiniaComanda)
                .HasForeignKey(d => d.IdProducte)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_LiniaComanda_Producte");*/
        });

        modelBuilder.Entity<LiniaUsuari>(entity =>
        {
            entity.HasKey(e => e.IdLiniaUsuari).HasName("PK__LiniaUsu__CE91A8DFC45B8071");

            entity.ToTable("LiniaUsuari");

            entity.HasIndex(e => new { e.IdUsuari, e.IdComanda }, "UQ_LiniaUsuari").IsUnique();

            entity.Property(e => e.IdLiniaUsuari).HasColumnName("idLiniaUsuari");
            entity.Property(e => e.IdComanda).HasColumnName("idComanda");
            entity.Property(e => e.IdUsuari).HasColumnName("idUsuari");

            entity.HasOne(d => d.IdComandaNavigation).WithMany(p => p.LiniaUsuaris)
                .HasForeignKey(d => d.IdComanda)
                .HasConstraintName("FK_LiniaUsuari_Comanda");

            entity.HasOne(d => d.IdUsuariNavigation).WithMany(p => p.LiniaUsuaris)
                .HasForeignKey(d => d.IdUsuari)
                .HasConstraintName("FK_LiniaUsuari_Usuari");
        });

        modelBuilder.Entity<Producte>(entity =>
        {
            entity.HasKey(e => e.IdProducte).HasName("PK__Producte__07F4A108AFAB5A10");

            entity.ToTable("Producte");

            entity.Property(e => e.IdProducte).HasColumnName("idProducte");
            entity.Property(e => e.Actiu)
                .HasDefaultValue(true)
                .HasColumnName("actiu");
            entity.Property(e => e.Descripcio)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("descripcio");
            entity.Property(e => e.IdCategoria).HasColumnName("idCategoria");
            entity.Property(e => e.Nom)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasColumnName("nom");
            entity.Property(e => e.Preu)
                .HasColumnType("decimal(10, 2)")
                .HasColumnName("preu");

            entity.HasOne(d => d.Categoria).WithMany(p => p.Productes)
                .HasForeignKey(d => d.IdCategoria)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK_Producte_Categoria");
        });

        modelBuilder.Entity<Taula>(entity =>
        {
            entity.HasKey(e => e.IdTaula).HasName("PK__Taula__70D1B07EDAFDDA3A");

            entity.ToTable("Taula");

            entity.Property(e => e.IdTaula).HasColumnName("idTaula");

            entity.Property(e => e.Capacitat).HasColumnName("capacitat");

            entity.Property(e => e.Estat)
                .HasColumnName("estat");

            entity.Property(e => e.Numero).HasColumnName("numero");
        });

        modelBuilder.Entity<Usuari>(entity =>
        {
            entity.HasKey(e => e.IdUsuari).HasName("PK__Usuari__46E684EC6D3F3C39");

            entity.ToTable("Usuari");

            entity.Property(e => e.IdUsuari).HasColumnName("idUsuari");
            entity.Property(e => e.Actiu)
                .HasDefaultValue(true)
                .HasColumnName("actiu");
            entity.Property(e => e.Admin).HasColumnName("admin");
            entity.Property(e => e.Contrasenya)
                .HasMaxLength(255)
                .IsUnicode(false)
                .HasColumnName("contrasenya");
            entity.Property(e => e.NomUsuari)
                .HasMaxLength(100)
                .IsUnicode(false)
                .HasColumnName("nomUsuari");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
