const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerOwners, obtenerOwner,
  crearOwner, actualizarOwner, eliminarOwner,
} = require('../controllers/ownerController');

router.get('/',    proteger, obtenerOwners);
router.get('/:id', proteger, obtenerOwner);
router.post('/',   proteger, crearOwner);
router.put('/:id', proteger, actualizarOwner);
router.delete('/:id', proteger, eliminarOwner);

module.exports = router;