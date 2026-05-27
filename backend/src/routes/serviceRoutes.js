const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerServices, crearService,
  actualizarService, eliminarService,
} = require('../controllers/serviceController');

router.get('/',    proteger, obtenerServices);
router.post('/',   proteger, crearService);
router.put('/:id', proteger, actualizarService);
router.delete('/:id', proteger, eliminarService);

module.exports = router;