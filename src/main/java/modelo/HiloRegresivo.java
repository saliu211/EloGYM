package modelo;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class HiloRegresivo extends Thread {
	private final JLabel label;
	private final String prefijo;
	private int restantes;
	private final Runnable alTerminar;
	private volatile boolean terminar = false;
	private volatile boolean parar = false;

	public HiloRegresivo(JLabel label, String prefijo, int segundos, Runnable alTerminar) {
		this.label = label;
		this.prefijo = prefijo;
		this.restantes = Math.max(segundos, 0);
		this.alTerminar = alTerminar;
	}

	private void pintar() {
		final String txt = prefijo + String.format("%02d:%02d", restantes / 60, restantes % 60);
		SwingUtilities.invokeLater(() -> label.setText(txt));
	}

	@Override
	public void run() {
		pintar();
		while (restantes > 0 && !terminar) {
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
			restantes--;
			pintar();
		}
		// Solo avisa si terminó solo; si lo cancelan (Salir), no continúa la cadena
		if (!terminar && alTerminar != null) {
			SwingUtilities.invokeLater(alTerminar);
		}
	}

	public void terminar() {
		terminar = true;
	}

	public void cambiarEstado() {
		parar = !parar;
	}
}