const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerPets, obtenerPet, crearPet,
  actualizarPet, eliminarPet,
  obtenerHistorial, agregarHistorial,
  obtenerVacunas, agregarVacuna,
} = require('../controllers/petController');

router.get('/',    proteger, obtenerPets);
router.get('/:id', proteger, obtenerPet);
router.post('/',   proteger, crearPet);
router.put('/:id', proteger, actualizarPet);
router.delete('/:id', proteger, eliminarPet);
router.get('/:id/medical-history',  proteger, obtenerHistorial);
router.post('/:id/medical-history', proteger, agregarHistorial);
router.get('/:id/vaccines',         proteger, obtenerVacunas);
router.post('/:id/vaccines',        proteger, agregarVacuna);

module.exports = router;