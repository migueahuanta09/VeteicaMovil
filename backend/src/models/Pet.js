// src/models/Pet.js

const mongoose = require('mongoose');

const HistorialSchema = new mongoose.Schema({
  consulta:    { type: String, required: true },
  fecha:       { type: String, required: true }, // yyyy-MM-dd
  diagnostico: { type: String },
  veterinario: { type: String },
});

const VacunaSchema = new mongoose.Schema({
  nombre:   { type: String, required: true },
  cantidad: { type: String },
  fecha:    { type: String, required: true }, // yyyy-MM-dd
});

const PetSchema = new mongoose.Schema(
  {
    nombre:          { type: String, required: true, trim: true },
    especie:         { type: String, required: true }, // "Perro", "Gato", etc.
    raza:            { type: String, required: true },
    edad:            { type: Number, required: true },
    peso:            { type: Number },
    sexo:            { type: String, enum: ['Macho', 'Hembra'] },
    color:           { type: String },
    fechaNacimiento: { type: String }, // yyyy-MM-dd
    nombreDueno:     { type: String, required: true },
    telefonoDueno:   { type: String },
    direccionDueno:  { type: String },
    notas:           { type: String },
    fotoUrl:         { type: String },
    ownerId:         { type: mongoose.Schema.Types.ObjectId, ref: 'Owner' },

    historialClinico: [HistorialSchema],
    vacunas:          [VacunaSchema],
  },
  { timestamps: true }
);

module.exports = mongoose.model('Pet', PetSchema);