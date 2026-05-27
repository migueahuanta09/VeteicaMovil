const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  obtenerProducts, obtenerProduct,
  crearProduct, actualizarProduct, eliminarProduct,
} = require('../controllers/productController');

router.get('/',    proteger, obtenerProducts);
router.get('/:id', proteger, obtenerProduct);
router.post('/',   proteger, crearProduct);
router.put('/:id', proteger, actualizarProduct);
router.delete('/:id', proteger, eliminarProduct);

module.exports = router;