const Service = require('../models/Service');

const obtenerServices = async (req, res) => {
  try {
    const items = await Service.find();
    res.json({ success: true, data: { items, total: items.length } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const crearService = async (req, res) => {
  try {
    const service = new Service(req.body);
    const guardado = await service.save();
    res.status(201).json({ success: true, data: guardado, message: 'Servicio creado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const actualizarService = async (req, res) => {
  try {
    const service = await Service.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
    if (!service) return res.status(404).json({ success: false, error: { code: 'DB_001', message: 'Servicio no encontrado' } });
    res.json({ success: true, data: service, message: 'Servicio actualizado' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const eliminarService = async (req, res) => {
  try {
    const service = await Service.findByIdAndDelete(req.params.id);
    if (!service) return res.status(404).json({ success: false, error: { code: 'DB_001', message: 'Servicio no encontrado' } });
    res.json({ success: true, message: 'Servicio eliminado' });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerServices, crearService, actualizarService, eliminarService };