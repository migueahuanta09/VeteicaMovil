const Pet         = require('../models/Pet');
const Appointment = require('../models/Appointment');

const obtenerDashboard = async (req, res) => {
  try {
    const totalPacientes = await Pet.countDocuments();

    const hoy = new Date().toISOString().split('T')[0]; // yyyy-MM-dd
    const citasHoy = await Appointment.countDocuments({ fecha: hoy });

    const proximasCitas = await Appointment.find({
      fecha:  { $gte: hoy },
      estado: { $in: ['Pendiente', 'Confirmada'] },
    })
      .sort({ fecha: 1, hora: 1 })
      .limit(5)
      .select('fecha hora nombreMascota nombreDueno veterinario');

    // Estadísticas de mascotas por especie
    const especiesRaw = await Pet.aggregate([
      { $group: { _id: '$especie', count: { $sum: 1 } } },
    ]);
    const totalMascotas = especiesRaw.reduce((acc, e) => acc + e.count, 0) || 1;
    const estadisticasMascotas = {};
    especiesRaw.forEach((e) => {
      estadisticasMascotas[e._id || 'Otro'] = Math.round((e.count / totalMascotas) * 100);
    });

    // Estadísticas de enfermedades por diagnóstico
    const diagRaw = await Appointment.aggregate([
      { $match: { diagnostico: { $ne: null, $ne: '' } } },
      { $group: { _id: '$diagnostico', count: { $sum: 1 } } },
      { $sort: { count: -1 } },
      { $limit: 3 },
    ]);
    const totalDiag = diagRaw.reduce((acc, d) => acc + d.count, 0) || 1;
    const estadisticasEnfermedades = {};
    diagRaw.forEach((d) => {
      estadisticasEnfermedades[d._id] = Math.round((d.count / totalDiag) * 100);
    });

    res.json({
      success: true,
      data: {
        nombreVeterinario: req.usuario ? `${req.usuario.nombre} ${req.usuario.apellido}` : 'Veterinario',
        totalPacientes,
        citasHoy,
        proximasCitas,
        estadisticasEnfermedades: Object.keys(estadisticasEnfermedades).length
          ? estadisticasEnfermedades
          : { 'Sin datos': 100 },
        estadisticasMascotas: Object.keys(estadisticasMascotas).length
          ? estadisticasMascotas
          : { 'Sin datos': 100 },
      },
    });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerDashboard };