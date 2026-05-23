const multer = require('multer');
const path   = require('path');
const Pet    = require('../models/Pet');
const Owner  = require('../models/Owner');

// Configuración de multer — dónde y cómo guardar las fotos
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'src/uploads/');
  },
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname);
    cb(null, `${req.params.tipo}_${req.params.id}_${Date.now()}${ext}`);
  },
});

const upload = multer({
  storage,
  limits: { fileSize: 5 * 1024 * 1024 }, // 5 MB máximo
  fileFilter: (req, file, cb) => {
    const permitidos = /jpeg|jpg|png/;
    const esValido = permitidos.test(path.extname(file.originalname).toLowerCase());
    esValido ? cb(null, true) : cb(new Error('Solo se permiten imágenes jpg/png'));
  },
});

// POST /api/upload/pet/:id
const subirFotoPet = [
  upload.single('foto'),
  async (req, res) => {
    try {
      if (!req.file) return res.status(400).json({ success: false, error: { code: 'DB_001', message: 'No se recibió archivo' } });
      const fotoUrl = `http://localhost:3000/uploads/${req.file.filename}`;
      await Pet.findByIdAndUpdate(req.params.id, { fotoUrl });
      res.json({ success: true, data: { fotoUrl }, message: 'Foto subida correctamente' });
    } catch (error) {
      res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
    }
  },
];

// POST /api/upload/owner/:id
const subirFotoOwner = [
  upload.single('foto'),
  async (req, res) => {
    try {
      if (!req.file) return res.status(400).json({ success: false, error: { code: 'DB_001', message: 'No se recibió archivo' } });
      const fotoUrl = `http://localhost:3000/uploads/${req.file.filename}`;
      res.json({ success: true, data: { fotoUrl }, message: 'Foto subida' });
    } catch (error) {
      res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
    }
  },
];

module.exports = { subirFotoPet, subirFotoOwner };