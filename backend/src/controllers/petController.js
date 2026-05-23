// src/controllers/petController.js

const Pet = require('../models/Pet');

// GET /api/pets
const obtenerPets = async (req, res) => {
  try {
    const pets = await Pet.find().select('-historialClinico -vacunas'); // lista resumida
    res.json({
      success: true,
      data: { items: pets, total: pets.length },
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// GET /api/pets/:id
const obtenerPet = async (req, res) => {
  try {
    const pet = await Pet.findById(req.params.id);
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    res.json({ success: true, data: pet });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// POST /api/pets
const crearPet = async (req, res) => {
  try {
    const pet = new Pet(req.body);
    const guardado = await pet.save();
    res.status(201).json({
      success: true,
      data: guardado,
      message: 'Mascota creada',
    });
  } catch (error) {
    res.status(422).json({
      success: false,
      error: { code: 'PET_002', message: error.message },
    });
  }
};

// PUT /api/pets/:id
const actualizarPet = async (req, res) => {
  try {
    const pet = await Pet.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    res.json({ success: true, data: pet, message: 'Mascota actualizada' });
  } catch (error) {
    res.status(422).json({
      success: false,
      error: { code: 'PET_002', message: error.message },
    });
  }
};

// DELETE /api/pets/:id
const eliminarPet = async (req, res) => {
  try {
    const pet = await Pet.findByIdAndDelete(req.params.id);
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    res.json({ success: true, message: 'Mascota eliminada' });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// GET /api/pets/:id/medical-history
const obtenerHistorial = async (req, res) => {
  try {
    const pet = await Pet.findById(req.params.id).select('historialClinico');
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    res.json({ success: true, data: pet.historialClinico });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// POST /api/pets/:id/medical-history
const agregarHistorial = async (req, res) => {
  try {
    const pet = await Pet.findById(req.params.id);
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    pet.historialClinico.push(req.body);
    await pet.save();
    const nuevo = pet.historialClinico[pet.historialClinico.length - 1];
    res.status(201).json({ success: true, data: nuevo, message: 'Historial agregado' });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// GET /api/pets/:id/vaccines
const obtenerVacunas = async (req, res) => {
  try {
    const pet = await Pet.findById(req.params.id).select('vacunas');
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    res.json({ success: true, data: pet.vacunas });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

// POST /api/pets/:id/vaccines
const agregarVacuna = async (req, res) => {
  try {
    const pet = await Pet.findById(req.params.id);
    if (!pet) {
      return res.status(404).json({
        success: false,
        error: { code: 'PET_001', message: 'Mascota no encontrada' },
      });
    }
    pet.vacunas.push(req.body);
    await pet.save();
    const nueva = pet.vacunas[pet.vacunas.length - 1];
    res.status(201).json({ success: true, data: nueva, message: 'Vacuna agregada' });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: { code: 'DB_001', message: error.message },
    });
  }
};

module.exports = {
  obtenerPets,
  obtenerPet,
  crearPet,
  actualizarPet,
  eliminarPet,
  obtenerHistorial,
  agregarHistorial,
  obtenerVacunas,
  agregarVacuna,
};