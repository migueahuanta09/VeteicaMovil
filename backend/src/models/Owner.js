const mongoose = require('mongoose');

const OwnerSchema = new mongoose.Schema(
  {
    nombre:    { type: String, required: true, trim: true },
    telefono:  { type: String, required: true },
    email:     { type: String, required: true },
    direccion: { type: String, required: true },
  },
  { timestamps: true }
);

module.exports = mongoose.model('Owner', OwnerSchema);