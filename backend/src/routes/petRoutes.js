// src/routes/petRoutes.js

const express = require('express');
const router = express.Router();

const {
  obtenerPets,
  obtenerPet,
  crearPet,
  actualizarPet,
  eliminarPet,
  obtenerHistorial,
  agregarHistorial,
  obtenerVacunas,
  agregarVacuna,
} = require('../controllers/petController');

router.get('/',    obtenerPets);
router.get('/:id', obtenerPet);
router.post('/',   crearPet);
router.put('/:id', actualizarPet);
router.delete('/:id', eliminarPet);

router.get('/:id/medical-history',  obtenerHistorial);
router.post('/:id/medical-history', agregarHistorial);
router.get('/:id/vaccines',         obtenerVacunas);
router.post('/:id/vaccines',        agregarVacuna);

module.exports = router;