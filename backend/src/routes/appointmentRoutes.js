const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerAppointments, obtenerAppointment,
  crearAppointment, actualizarAppointment, eliminarAppointment,
  completarAppointment, cancelarAppointment,
} = require('../controllers/appointmentController');

router.get('/',    proteger, obtenerAppointments);
router.get('/:id', proteger, obtenerAppointment);
router.post('/',   proteger, crearAppointment);
router.put('/:id', proteger, actualizarAppointment);
router.delete('/:id', proteger, eliminarAppointment);
router.put('/:id/complete', proteger, completarAppointment);
router.put('/:id/cancel',   proteger, cancelarAppointment);

module.exports = router;