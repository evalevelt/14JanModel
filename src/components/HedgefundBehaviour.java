package components;

/**
 * Created by eva on 24/01/2017.
 */
public class HedgefundBehaviour {
    Hedgefund hedgefund;

    public HedgefundBehaviour(){
        this.hedgefund=null;
    }

    public void printBank(){
        System.out.println(this.hedgefund.name);
    }

    public void updateBalanceSheet(){
        this.hedgefund.Balancesheet.phi++;
    }


}
