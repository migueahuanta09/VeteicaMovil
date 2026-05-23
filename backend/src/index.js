// src/index.js

const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const app = express();

app.use(cors());
app.use(express.json());

// ── Rutas ──────────────────────────────────────
const petRoutes = require('./routes/petRoutes');
app.use('/api/pets', petRoutes);

const ownerRoutes = require('./routes/ownerRoutes');
app.use('/api/owners', ownerRoutes);

const appointmentRoutes = require('./routes/appointmentRoutes');
app.use('/api/appointments', appointmentRoutes);

const productRoutes = require('./routes/productRoutes');
const serviceRoutes = require('./routes/serviceRoutes');
app.use('/api/products', productRoutes);
app.use('/api/services', serviceRoutes);

const paymentRoutes = require('./routes/paymentRoutes');
app.use('/api/payments', paymentRoutes);

const authRoutes = require('./routes/authRoutes');
app.use('/api/auth', authRoutes);

const dashboardRoutes = require('./routes/dashboardRoutes');
const uploadRoutes    = require('./routes/uploadRoutes');
app.use('/api/dashboard', dashboardRoutes);
app.use('/api/upload',    uploadRoutes);

// Esta línea sirve las fotos como archivos estáticos
app.use('/uploads', express.static('src/uploads'));

// ───────────────────────────────────────────────

app.get('/', (req, res) => {
  res.json({ mensaje: 'API Veterinaria funcionando 🐾' });
});

mongoose.connect(process.env.MONGODB_URI)
  .then(() => console.log('✅ Conectado a MongoDB'))
  .catch((err) => console.error('❌ Error al conectar:', err));

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Servidor corriendo en puerto ${PORT}`);
});