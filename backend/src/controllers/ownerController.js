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
    const mongoose = require('mongoose');
    const { mascotaIds, ...ownerFields } = req.body;

    const owner = await Owner.findByIdAndUpdate(req.params.id, ownerFields, { new: true, runValidators: true });
    if (!owner) {
      return res.status(404).json({ success: false, error: { code: 'OWNER_001', message: 'Dueño no encontrado' } });
    }

    // Si se envía mascotaIds, sincronizamos las vinculaciones
    if (Array.isArray(mascotaIds)) {
      // Convertir strings a ObjectId para que Mongoose los compare correctamente
      const petObjectIds = mascotaIds
        .filter(id => mongoose.Types.ObjectId.isValid(id))
        .map(id => new mongoose.Types.ObjectId(id));

      // Quitar el ownerId a mascotas que ya no están en la lista
      await Pet.updateMany(
        { ownerId: owner._id, _id: { $nin: petObjectIds } },
        { $unset: { ownerId: '' }, $set: { nombreDueno: '' } }
      );

      // Asignar el ownerId a las mascotas seleccionadas
      if (petObjectIds.length > 0) {
        await Pet.updateMany(
          { _id: { $in: petObjectIds } },
          { $set: { ownerId: owner._id, nombreDueno: owner.nombre } }
        );
      }
    }

    const mascotas = await Pet.find({ ownerId: owner._id }).select('nombre especie');
    res.json({ success: true, data: { ...owner.toObject(), mascotas }, message: 'Dueño actualizado' });
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