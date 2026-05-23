const Product = require('../models/Product');

const obtenerProducts = async (req, res) => {
  try {
    const items = await Product.find();
    res.json({ success: true, data: { items, total: items.length } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const obtenerProduct = async (req, res) => {
  try {
    const product = await Product.findById(req.params.id);
    if (!product) return res.status(404).json({ success: false, error: { code: 'DB_001', message: 'Producto no encontrado' } });
    res.json({ success: true, data: product });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const crearProduct = async (req, res) => {
  try {
    const product = new Product(req.body);
    const guardado = await product.save();
    res.status(201).json({ success: true, data: guardado, message: 'Producto creado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const actualizarProduct = async (req, res) => {
  try {
    const product = await Product.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
    if (!product) return res.status(404).json({ success: false, error: { code: 'DB_001', message: 'Producto no encontrado' } });
    res.json({ success: true, data: product, message: 'Producto actualizado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const eliminarProduct = async (req, res) => {
  try {
    const product = await Product.findByIdAndDelete(req.params.id);
    if (!product) return res.status(404).json({ success: false, error: { code: 'DB_001', message: 'Producto no encontrado' } });
    res.json({ success: true, message: 'Producto eliminado' });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerProducts, obtenerProduct, crearProduct, actualizarProduct, eliminarProduct };