const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerPendientes, cobrarPago, obtenerTicket,
} = require('../controllers/paymentController');

router.get('/pending',      proteger, obtenerPendientes);
router.post('/:id/charge',  proteger, cobrarPago);
router.get('/:id/ticket',   proteger, obtenerTicket);

module.exports = router;