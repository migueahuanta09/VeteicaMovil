const mongoose = require('mongoose');

const PendingPaymentSchema = new mongoose.Schema(
  {
    nombreMascota:   { type: String, required: true },
    nombreServicio:  { type: String, default: 'Consulta general' },
    fecha:           { type: String, required: true },
    total:           { type: Number, default: 0 },
    estado:          { type: String, enum: ['Pendiente', 'Cobrado'], default: 'Pendiente' },
    metodoPago:      { type: String },
    montoPagado:     { type: Number },
    fechaCobro:      { type: String },
    appointmentId:   { type: mongoose.Schema.Types.ObjectId, ref: 'Appointment' },
  },
  { timestamps: true }
);

module.exports = mongoose.model('PendingPayment', PendingPaymentSchema);