package application.process;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;
import application.process.Reader;
import application.process.Writer;
import javafx.animation.PathTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class RWprocesscontroller {

	// private Main main;
	@FXML
	public TextField numreader;
	public TextField numwriter;
	private final IntegerProperty numr;
	private final IntegerProperty numw;

	@FXML private ImageView book;
	@FXML private ImageView eye;
	@FXML private ImageView pen;
	@FXML private ImageView queue;
	@FXML private ImageView i0; // reader
	@FXML private ImageView i1;
	@FXML private ImageView i2;
	@FXML private ImageView i3;
	@FXML private ImageView i4;
	@FXML private ImageView i5; // writer
	@FXML private ImageView i6;

	public String nread;
	public String nwrite;
	public static int READERS;
	public static int WRITERS;
	public Label w;
	public Label r;
	private int a;
	private int b;
	public int count = 0;
	public int numqueue=0;

	PathTransition [] path= new PathTransition[7];
	ImageView[] icon = new ImageView[7];
	Path[] p= new Path[7];

	/** GUI gets input from text field */
	public RWprocesscontroller() {
		this.numr = new SimpleIntegerProperty(0);
		this.numw = new SimpleIntegerProperty(0);
	}

	public void getreader() {

		try {
			a = Integer.parseInt(numreader.getText());
		} catch (NumberFormatException ex) {
			System.out.println("not a number");
		}
		r.setText("the content of reader " + a);
		numr.setValue(a);
	}

	public void getwriter() {

		try {
			b = Integer.parseInt(numwriter.getText());
		} catch (NumberFormatException ex) {
			System.out.println("not a number");
		}
		w.setText("the content of writer" + b);
		numw.setValue(b);
	}

	/** main process starts */
	public void GO() {
		icon[0] = i0;
		icon[1] = i1;
		icon[2] = i2;
		icon[3] = i3;
		icon[4] = i4;
		icon[5] = i5;
		icon[6] = i6;
		for (int i = 0; i < 7; i++) {
			p[i]= new Path();
			path[i]=new PathTransition();
		}		
		getreader();
		getwriter();
		READERS = numr.getValue();
		WRITERS = numw.getValue();
		System.out.print(READERS + " " + WRITERS + "\n");
		Database database = new Database();
		for (int i = 0; i < READERS; i++) {
			new Reader(database).start();
		}
		for (int i = 0; i < WRITERS; i++) {
			new Writer(database).start();
		}

	}

	class Database extends Thread {
		int readers = 0; // number of active readers
		int read_or_write = 0;
		Queue<Integer> w = new LinkedList<Integer>();
		boolean readentry = true;
		boolean writeentry = true;
		Random rand = new Random();
		int index=0;
		
		public double exprand(float lambda) {
			return Math.log(1 - rand.nextDouble()) / (-lambda);
		}

		void readjudge() {
			if (this.readers == 0 && w.isEmpty()) {
				this.read_or_write = 0;
				this.readentry = true;
				this.writeentry = true;
			} else if (this.readers == 0 && !w.isEmpty()) {
				this.read_or_write = 2;
				this.readentry = true;
			}
			this.notifyAll();
		}

		void writejudge() {
			if (this.readers == 0 && w.isEmpty()) {
				this.read_or_write = 0;
				this.readentry = true;
				this.writeentry = true;
			} else if (this.readers != 0 && w.isEmpty()) {
				this.read_or_write = 1;
				this.writeentry = true;
			}
			this.notifyAll();
		}

		/** reader */
		public void read(int number) {
			
			synchronized (this) {
				System.out.println("-people in Qline- " + numqueue);
				System.out.println("Reader " + number + " want entry!");
				if (read_or_write == 0)
					read_or_write = 1;
				if (read_or_write == 2)
					writeentry = false;
			}
			synchronized (this) {
				while (readentry == false) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				this.readers++;
				index=this.readers-1;
				if(index<0) index=0;
				System.out.println("---------"+this.readers);
				create("READER",number,numqueue);
				System.out.println("Reader " + number + " is in line.");
				numqueue++;
				System.out.println("-people in Qline- " + numqueue);
			}
			synchronized (this) {
				while (read_or_write == 2) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				hi(number,count,"READER");
				System.out.println("Reader " + number + " Start reading. ");
				numqueue--;
				count++;
				System.out.println("-people in Qline- " + numqueue);
				System.out.println("-people in Bline- " + count);

			}
			try {
				int a = (int) (exprand(0.5f) * 1000);
				Thread.sleep(a);
				// Thread.sleep((int) (Math.random() * DELAY));
			} catch (InterruptedException e) {
			}
			synchronized (this) {
				System.out.println("Reader " + number + " stops reading.");
				count--;
				System.out.println("-people in Bline- " + count);
				this.readers--;
				readjudge();
			}
		}

		/** writer */
		public void write(int number) {
			System.out.println("-people in Qline- " + numqueue);
			System.out.println("Writer " + number + " want entry!");
			if (read_or_write == 0)
				read_or_write = 2;
			if (read_or_write == 1)
				readentry = false;
			synchronized (this) {
				while (writeentry == false) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				create("WRITER",number+5,numqueue);
				System.out.println("Writer " + number + " is in line");
				numqueue++;
				System.out.println("-people in Qline- " + numqueue);
				w.offer(number);
			}
			synchronized (this) {
				while (read_or_write == 1) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			synchronized (this) {
				while (w.peek() != number) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				hi(number+5,1,"WRITER");
				System.out.println("Writer " + number + " starts writing.");
				count++;
				numqueue--;
				System.out.println("-people in Qline- " + numqueue);
				System.out.println("-people in Bline- " + count);
			}
			final int DELAY = 1000;
			try {
				int a = (int) (exprand(0.5f) * DELAY);
				Thread.sleep(a);
				// System.out.println(a);
				// Thread.sleep((int) (Math.random() * DELAY));
			} catch (InterruptedException e) {
			}
			synchronized (this) {
				count--;
				System.out.println("Writer " + number + " stops writing.");
				System.out.println("-people in Bline- " + count);
				w.poll();
				writejudge();
			}
		}
	}
	public void create(String name, int n,int people) {

		photoset(icon[n],name);
		pathset(p[n],name,people,n);
		path[n].setNode(icon[n]);
		move(path[n],p[n]);
	}
    double[] loc= new double[7];
	public void pathset(Path p,String name,int people,int n) {
		switch(name) {
		case "READER":
			p.getElements().add(new MoveTo(-20,-400));
			break;
		case "WRITER":
			p.getElements().add(new MoveTo(-40,-380));
			break;
		}
		loc[n]=getX(people,name);
		p.getElements().add(new LineTo(loc[n],-180));
	}
	public double getX(int people,String name) {
		double i=0;
		switch(name) {
		case "READER":
		    i= -100+70*people;
		    break;
		case "WRITER":
			i= -230+70*people;
			break;
		}
		return i;
	}
	public void hi(int n,int people,String name) {
		p[n].getElements().clear();
		p[n].getElements().add(new MoveTo(loc[n],-180));
		p[n].getElements().add(new LineTo(getF(people,name),0));
		move(path[n],p[n]);
	}
	public double getF(int people,String name) {
		double i=0;
		switch(name) {
		case "READER":
			i=-80+80*people;
			break;
		case "WRITER":
			i=-200;
			break;
		}
		return i;
	}
	
	public void move(PathTransition t, Path p) {
		t.setDuration(Duration.seconds(4));
		t.setPath(p);
		t.play();
	}


	public void photoset(ImageView i, String name) {
		switch (name) {
		case "READER":
			i.setImage(new Image("file:src/application/EYE%20copy.png"));
			break;
		case "WRITER":
			i.setImage(new Image("file:src/application/PEN%20copy.png"));
			break;
		
		}
		i.setFitHeight(60);
		i.setFitWidth(60);
	}

	/** Homebutton */
	@FXML
	private void gohome() {
		Main.showHome();
	}
}