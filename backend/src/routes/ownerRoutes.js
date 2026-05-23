const express = require('express');
const router  = express.Router();
const { obtenerOwners, obtenerOwner, crearOwner, actualizarOwner, eliminarOwner } = require('../controllers/ownerController');

router.get('/',       obtenerOwners);
router.get('/:id',    obtenerOwner);
router.post('/',      crearOwner);
router.put('/:id',    actualizarOwner);
router.delete('/:id', eliminarOwner);

module.exports = router;