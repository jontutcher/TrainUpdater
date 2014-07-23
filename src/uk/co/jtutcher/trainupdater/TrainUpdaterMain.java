package uk.co.jtutcher.trainupdater;

public class TrainUpdaterMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
			StardogWriter stardog = new StardogWriter();
			//w needs opening and closing!
			stardog.open();
			JenaTrainUpdater updater = new JenaTrainUpdater(stardog);
//			t.doUpdate(0);
			updater.connect();
			TrainUpdateService scheduler = new TrainUpdateService(updater);
			scheduler.startAsync();
			System.out.println("Press Enter to exit");
			System.in.read();
			scheduler.stopAsync();
			stardog.close();
			updater.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
