const Owner = require('../models/Owner');
const Pet   = require('../models/Pet');

// GET /api/owners
const obtenerOwners = async (req, res) => {
  try {
    const owners = await Owner.find();
    const items = await Promise.all(
      owners.map(async (owner) => {
        const cantidad = await Pet.countDocuments({ ownerId: owner._id });
        return { ...owner.toObject(), cantidadMascotas: cantidad };
      })
    );
    res.json({ success: true, data: { items, total: items.length } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

// GET /api/owners/:id
const obtenerOwner = async (req, res) => {
  try {
    const owner = await Owner.findById(req.params.id);
    if (!owner) {
      return res.status(404).json({ success: false, error: { code: 'OWNER_001', message: 'Dueño no encontrado' } });
    }
    const mascotas = await Pet.find({ ownerId: owner._id }).select('nombre especie');
    res.json({ success: true, data: { ...owner.toObject(), mascotas } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

// POST /api/owners
const crearOwner = async (req, res) => {
  try {
    const owner = new Owner(req.body);
    const guardado = await owner.save();
    res.status(201).json({ success: true, data: guardado, message: 'Dueño creado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'OWNER_001', message: error.message } });
  }
};

// PUT /api/owners/:id
const actualizarOwner = async (req, res) => {
  try {
    const owner = await Owner.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
    if (!owner) {
      return res.status(404).json({ success: false, error: { code: 'OWNER_001', message: 'Dueño no encontrado' } });
    }
    res.json({ success: true, data: owner, message: 'Dueño actualizado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'OWNER_001', message: error.message } });
  }
};

// DELETE /api/owners/:id
const eliminarOwner = async (req, res) => {
  try {
    const owner = await Owner.findByIdAndDelete(req.params.id);
    if (!owner) {
      return res.status(404).json({ success: false, error: { code: 'OWNER_001', message: 'Dueño no encontrado' } });
    }
    res.json({ success: true, message: 'Dueño eliminado' });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerOwners, obtenerOwner, crearOwner, actualizarOwner, eliminarOwner };