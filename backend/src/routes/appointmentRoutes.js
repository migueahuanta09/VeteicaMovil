const express = require('express');
const router  = express.Router();
const { obtenerAppointments, obtenerAppointment, crearAppointment, actualizarAppointment, eliminarAppointment, completarAppointment, cancelarAppointment } = require('../controllers/appointmentController');

router.get('/',               obtenerAppointments);
router.get('/:id',            obtenerAppointment);
router.post('/',              crearAppointment);
router.put('/:id',            actualizarAppointment);
router.delete('/:id',         eliminarAppointment);
router.put('/:id/complete',   completarAppointment);
router.put('/:id/cancel',     cancelarAppointment);

module.exports = router;