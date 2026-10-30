package modelo;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloCronometro extends Thread {
	private final JLabel label;
	private final String prefijo;
	private int total = 0;
	private volatile boolean terminar = false;
	private volatile boolean parar = false;

	public HiloCronometro(JLabel label) {
		this.label = label;
		this.prefijo = label.getText().split(":")[0] + ": ";
	}
	
	public HiloCronometro(JLabel label, String prefijo) {
	    this.label = label;
	    this.prefijo = prefijo;
	}

	@Override
	public void run() {
		while (!terminar) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (parar && !terminar) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (terminar)
				break;
			total++;
			final String txt = prefijo + String.format("%02d:%02d", total / 60, total % 60);
			SwingUtilities.invokeLater(() -> label.setText(txt));
		}
	}

	public void terminar() {
		terminar = true;
	}

	public void cambiarEstado() {
		parar = !parar;
	}

	public int getTotalSegundos() {
		return total;
	}
}