const express = require('express');
const router  = express.Router();
const { subirFotoPet, subirFotoOwner } = require('../controllers/uploadController');

router.post('/pet/:id',   subirFotoPet);
router.post('/owner/:id', subirFotoOwner);

module.exports = router;