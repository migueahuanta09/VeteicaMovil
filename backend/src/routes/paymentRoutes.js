const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerPendientes, cobrar, generarTicket,
} = require('../controllers/paymentController');

router.get('/pending',     proteger, obtenerPendientes);
router.post('/:id/charge', proteger, cobrar);
router.get('/:id/ticket',  proteger, generarTicket);

module.exports = router;