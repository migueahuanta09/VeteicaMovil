const mongoose = require('mongoose');

const AppointmentSchema = new mongoose.Schema(
  {
    nombreMascota: { type: String, required: true },
    fecha:         { type: String, required: true }, // yyyy-MM-dd
    hora:          { type: String, required: true }, // HH:MM AM/PM
    nombreDueno:   { type: String, required: true },
    veterinario:   { type: String, required: true },
    motivo:        { type: String },
    diagnostico:   { type: String },
    estado:        { type: String, enum: ['Pendiente', 'Confirmada', 'Completada', 'Cancelada'], default: 'Pendiente' },
    petId:         { type: mongoose.Schema.Types.ObjectId, ref: 'Pet' },
    ownerId:       { type: mongoose.Schema.Types.ObjectId, ref: 'Owner' },
  },
  { timestamps: true }
);

module.exports = mongoose.model('Appointment', AppointmentSchema);