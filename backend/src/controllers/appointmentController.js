const Appointment    = require('../models/Appointment');
const PendingPayment = require('../models/PendingPayment');

const obtenerAppointments = async (req, res) => {
  try {
    const items = await Appointment.find();
    res.json({ success: true, data: { items, total: items.length } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const obtenerAppointment = async (req, res) => {
  try {
    const appt = await Appointment.findById(req.params.id);
    if (!appt) return res.status(404).json({ success: false, error: { code: 'APPT_001', message: 'Cita no encontrada' } });
    res.json({ success: true, data: appt });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const crearAppointment = async (req, res) => {
  try {
    const appt = new Appointment(req.body);
    const guardado = await appt.save();
    res.status(201).json({ success: true, data: guardado, message: 'Cita creada' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'APPT_001', message: error.message } });
  }
};

const actualizarAppointment = async (req, res) => {
  try {
    const appt = await Appointment.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
    if (!appt) return res.status(404).json({ success: false, error: { code: 'APPT_001', message: 'Cita no encontrada' } });
    res.json({ success: true, data: appt, message: 'Cita actualizada' });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'APPT_001', message: error.message } });
  }
};

const eliminarAppointment = async (req, res) => {
  try {
    const appt = await Appointment.findByIdAndDelete(req.params.id);
    if (!appt) return res.status(404).json({ success: false, error: { code: 'APPT_001', message: 'Cita no encontrada' } });
    res.json({ success: true, message: 'Cita eliminada' });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const completarAppointment = async (req, res) => {
  try {
    const appt = await Appointment.findByIdAndUpdate(
      req.params.id,
      { estado: 'Completada', diagnostico: req.body.diagnostico },
      { new: true }
    );
    if (!appt) return res.status(404).json({ success: false, error: { code: 'APPT_001', message: 'Cita no encontrada' } });

    const cobro = new PendingPayment({
      nombreMascota:  appt.nombreMascota,
      nombreServicio: 'Consulta general',
      fecha:          appt.fecha,
      total:          500,
      appointmentId:  appt._id,
    });
    const cobroGuardado = await cobro.save();

    res.json({
      success: true,
      data: { cita: appt, cobroPendiente: cobroGuardado },
      message: 'Cita completada y cobro generado',
    });
  } catch (error) {
    console.error('ERROR completarAppointment:', error.message);
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

const cancelarAppointment = async (req, res) => {
  try {
    const appt = await Appointment.findByIdAndUpdate(req.params.id, { estado: 'Cancelada' }, { new: true });
    if (!appt) return res.status(404).json({ success: false, error: { code: 'APPT_001', message: 'Cita no encontrada' } });
    res.json({ success: true, data: { id: appt._id, estado: appt.estado }, message: 'Cita cancelada' });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerAppointments, obtenerAppointment, crearAppointment, actualizarAppointment, eliminarAppointment, completarAppointment, cancelarAppointment };