const mongoose = require('mongoose');

const ProductSchema = new mongoose.Schema(
  {
    nombre:         { type: String, required: true, trim: true },
    existencia:     { type: Number, required: true },
    precio:         { type: Number, required: true },
    fechaCaducidad: { type: String, required: true }, // yyyy-MM-dd
    dosis:          { type: String },
    indicaciones:   { type: String },
    formula:        { type: String },
    administracion: { type: String },
  },
  { timestamps: true }
);

module.exports = mongoose.model('Product', ProductSchema);