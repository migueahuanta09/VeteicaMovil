const express = require('express');
const router  = express.Router();
const { obtenerPendientes, cobrar, generarTicket } = require('../controllers/paymentController');

router.get('/pending',        obtenerPendientes);
router.post('/:id/charge',    cobrar);
router.get('/:id/ticket',     generarTicket);

module.exports = router;