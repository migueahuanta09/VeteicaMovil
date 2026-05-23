const express = require('express');
const router  = express.Router();
const { obtenerServices, crearService, actualizarService, eliminarService } = require('../controllers/serviceController');

router.get('/',       obtenerServices);
router.post('/',      crearService);
router.put('/:id',    actualizarService);
router.delete('/:id', eliminarService);

module.exports = router;