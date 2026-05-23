const express = require('express');
const router  = express.Router();
const { obtenerProducts, obtenerProduct, crearProduct, actualizarProduct, eliminarProduct } = require('../controllers/productController');

router.get('/',       obtenerProducts);
router.get('/:id',    obtenerProduct);
router.post('/',      crearProduct);
router.put('/:id',    actualizarProduct);
router.delete('/:id', eliminarProduct);

module.exports = router;