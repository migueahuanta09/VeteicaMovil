const mongoose = require('mongoose');

const ServiceSchema = new mongoose.Schema(
  {
    nombre:       { type: String, required: true, trim: true },
    cantidad:     { type: Number, required: true },
    veterinarios: { type: Number, required: true },
    precio:       { type: Number, required: true },
    descripcion:  { type: String },
  },
  { timestamps: true }
);

module.exports = mongoose.model('Service', ServiceSchema);  