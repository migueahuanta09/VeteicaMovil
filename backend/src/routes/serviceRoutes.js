const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerServices, obtenerService,
  crearService, actualizarService, eliminarService,
} = require('../controllers/serviceController');

router.get('/',    proteger, obtenerServices);
router.get('/:id', proteger, obtenerService);
router.post('/',   proteger, crearService);
router.put('/:id', proteger, actualizarService);
router.delete('/:id', proteger, eliminarService);

module.exports = router;