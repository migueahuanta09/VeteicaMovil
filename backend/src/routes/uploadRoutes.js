const express = require('express');
const router  = express.Router();
const { proteger } = require('../middleware/authMiddleware');
const {
  subirFotoPet, subirFotoOwner,
} = require('../controllers/uploadController');

router.post('/pet/:id',   proteger, subirFotoPet);
router.post('/owner/:id', proteger, subirFotoOwner);

module.exports = router;