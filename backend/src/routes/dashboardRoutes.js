const express  = require('express');
const router   = express.Router();
const { obtenerDashboard } = require('../controllers/dashboardController');
const { proteger } = require('../middleware/authMiddleware');

router.get('/', proteger, obtenerDashboard);

module.exports = router;