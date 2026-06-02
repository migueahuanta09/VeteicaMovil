const jwt  = require('jsonwebtoken');
const User = require('../models/User');

const generarToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, { expiresIn: '7d' });
};

// POST /api/auth/register
const register = async (req, res) => {
  try {
    const { nombre, apellido, cedula, email, password, telefono } = req.body;
    const yaExiste = await User.findOne({ email });
    if (yaExiste) {
      return res.status(400).json({ success: false, error: { code: 'AUTH_001', message: 'El email ya está registrado' } });
    }
    const user = new User({ nombre, apellido, cedula, email, password, telefono });
    await user.save();
    const token = generarToken(user._id);
    res.status(201).json({
      success: true,
      data: { token, user: { id: user._id, nombre: user.nombre, email: user.email } },
      message: 'Registro exitoso',
    });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

// POST /api/auth/login
const login = async (req, res) => {
  try {
    const { email, password, cedula } = req.body;
    const user = await User.findOne({ email });
    if (!user) {
      return res.status(401).json({ success: false, error: { code: 'AUTH_002', message: 'Usuario no encontrado' } });
    }
    const passwordCorrecta = await user.compararPassword(password);
    if (!passwordCorrecta) {
      return res.status(401).json({ success: false, error: { code: 'AUTH_001', message: 'Credenciales incorrectas' } });
    }
    if (cedula && user.cedula !== cedula) {
      return res.status(401).json({ success: false, error: { code: 'AUTH_004', message: 'Cédula incorrecta' } });
    }
    const token = generarToken(user._id);
    res.json({
      success: true,
      data: {
        token,
        user: {
          id:       user._id,
          nombre:   user.nombre,
          apellido: user.apellido,
          cedula:   user.cedula,
          email:    user.email,
          telefono: user.telefono,
        },
      },
      message: 'Login exitoso',
    });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

// POST /api/auth/logout
const logout = async (req, res) => {
  res.json({ success: true, message: 'Sesión cerrada' });
};

module.exports = { register, login, logout };