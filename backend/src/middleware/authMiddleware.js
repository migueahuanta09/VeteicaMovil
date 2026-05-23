const jwt  = require('jsonwebtoken');
const User = require('../models/User');

const proteger = async (req, res, next) => {
  try {
    // Leer el token del header Authorization: Bearer {token}
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({ success: false, error: { code: 'AUTH_003', message: 'Token no proporcionado' } });
    }

    const token = authHeader.split(' ')[1];
    const decoded = jwt.verify(token, process.env.JWT_SECRET);

    // Adjuntar el usuario a la petición para usarlo en los controladores
    req.usuario = await User.findById(decoded.id).select('-password');
    next();
  } catch (error) {
    res.status(401).json({ success: false, error: { code: 'AUTH_003', message: 'Token expirado o inválido' } });
  }
};

module.exports = { proteger };