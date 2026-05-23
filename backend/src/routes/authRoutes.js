const express = require('express');
const router  = express.Router();
const { register, login, logout } = require('../controllers/authController');
const { proteger } = require('../middleware/authMiddleware');

router.post('/register', register);
router.post('/login',    login);
router.post('/logout',   proteger, logout); // proteger requiere token válido

module.exports = router;