const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerAppointments, obtenerAppointment,
  crearAppointment, actualizarAppointment, eliminarAppointment,
  completarAppointment, cancelarAppointment,
} = require('../controllers/appointmentController');

router.get('/',    proteger, obtenerAppointments);
router.post('/',   proteger, crearAppointment);

// Estas van ANTES de /:id para que no sean capturadas por ella
router.put('/:id/complete', proteger, completarAppointment);
router.put('/:id/cancel',   proteger, cancelarAppointment);

router.get('/:id', proteger, obtenerAppointment);
router.put('/:id', proteger, actualizarAppointment);
router.delete('/:id', proteger, eliminarAppointment);

module.exports = router;