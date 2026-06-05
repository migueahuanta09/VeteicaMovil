const PendingPayment = require('../models/PendingPayment');

// GET /api/payments/pending
const obtenerPendientes = async (req, res) => {
  try {
    const items = await PendingPayment.find({ estado: 'Pendiente' }).sort({ createdAt: -1 });
    res.json({ success: true, data: { items, total: items.length } });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

// POST /api/payments/:id/charge
const cobrar = async (req, res) => {
  try {
    const cobro = await PendingPayment.findByIdAndUpdate(
      req.params.id,
      {
        estado:      'Cobrado',
        metodoPago:  req.body.metodoPago,
        montoPagado: req.body.montoPagado,
        fechaCobro:  new Date().toISOString().split('T')[0],
      },
      { new: true }
    );
    if (!cobro) return res.status(404).json({ success: false, error: { code: 'PAY_001', message: 'Cobro no encontrado' } });

    res.json({
      success: true,
      data: {
        ...cobro.toObject(),
        ticketUrl: `/api/payments/${cobro._id}/ticket`,
      },
      message: 'Cobro realizado exitosamente',
    });
  } catch (error) {
    res.status(422).json({ success: false, error: { code: 'PAY_002', message: error.message } });
  }
};

// GET /api/payments/:id/ticket
const generarTicket = async (req, res) => {
  try {
    const cobro = await PendingPayment.findById(req.params.id);
    if (!cobro) return res.status(404).json({ success: false, error: { code: 'PAY_001', message: 'Cobro no encontrado' } });

    res.json({
      success: true,
      data: {
        clinica:    'VETEICA CLINIC',
        mascota:    cobro.nombreMascota,
        servicio:   cobro.nombreServicio,
        fecha:      cobro.fecha,
        monto:      cobro.montoPagado,
        metodoPago: cobro.metodoPago,
        folio:      cobro._id,
      },
    });
  } catch (error) {
    res.status(500).json({ success: false, error: { code: 'DB_001', message: error.message } });
  }
};

module.exports = { obtenerPendientes, cobrar, generarTicket };