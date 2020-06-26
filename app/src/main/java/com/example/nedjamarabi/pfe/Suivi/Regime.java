package com.example.nedjamarabi.pfe.Suivi;

public class Regime {
    private String idUtilisateur;
    private float caloriesRecommandees, glucidesRecommandees, lipidesRecommandees, proteinesRecommandees;
    private float caloriePourRégime;
    
    public Regime() {
    }
    
    public Regime(String idUtilisateur, float caloriesRecommandees, float glucidesRecommandees, float lipidesRecommandees, float proteinesRecommandees) {
        this.idUtilisateur = idUtilisateur;
        this.caloriesRecommandees = caloriesRecommandees;
        this.glucidesRecommandees = glucidesRecommandees;
        this.lipidesRecommandees = lipidesRecommandees;
        this.proteinesRecommandees = proteinesRecommandees;
    }
    
    public Regime(Utilisateur utilisateur) {
        idUtilisateur = utilisateur.getIdUtilisateur();
        caloriesRecommandees = utilisateur.calculMetabolismeBase();
        glucidesRecommandees = 0;
        lipidesRecommandees = 0;
        proteinesRecommandees = 0;
    }
    
    public String getIdUtilisateur() {
        return idUtilisateur;
    }
    
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }
    
    public float getCaloriesRecommandees() {
        return caloriesRecommandees;
    }
    
    public void setCaloriesRecommandees(float caloriesRecommandees) {
        this.caloriesRecommandees = caloriesRecommandees;
    }
    
    public float getGlucidesRecommandees() {
        return glucidesRecommandees;
    }
    
    public void setGlucidesRecommandees(float glucidesRecommandees) {
        this.glucidesRecommandees = glucidesRecommandees;
    }
    
    public float getLipidesRecommandees() {
        return lipidesRecommandees;
    }
    
    public void setLipidesRecommandees(float lipidesRecommandees) {
        this.lipidesRecommandees = lipidesRecommandees;
    }
    
    public float getProteinesRecommandees() {
        return proteinesRecommandees;
    }
    
    public void setProteinesRecommandees(float proteinesRecommandees) {
        this.proteinesRecommandees = proteinesRecommandees;
    }
    
    
    public void calculPlanAlimentaire(Utilisateur utilisateur) {
        switch (utilisateur.getNiveau()) {
            case DEBUTANT:
                switch (utilisateur.getObjectif()) {
                    case PERTE_POIDS:
                        caloriePourRégime = -1000;
                        switch (utilisateur.getMorphotype()) {
                            case ECTOMORPHE:
                                proteinesRecommandees = 1.8f * utilisateur.getPoidsDesire();
                                lipidesRecommandees = 1 * utilisateur.getPoidsDesire();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case MESOMORPHE:
                                proteinesRecommandees = 2 * utilisateur.getPoidsDesire();
                                lipidesRecommandees = 1 * utilisateur.getPoidsDesire();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case ENDOMORPHE:
                                proteinesRecommandees = 2.2f * utilisateur.getPoidsDesire();
                                lipidesRecommandees = 1 * utilisateur.getPoidsDesire();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                        }
                        break;
                    case GAIN_MASSE:
                        caloriePourRégime = 300;
                        switch (utilisateur.getMorphotype()) {
                            case ECTOMORPHE:
                                proteinesRecommandees = 1.8f * utilisateur.getPoids();
                                lipidesRecommandees = 1 * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case MESOMORPHE:
                                proteinesRecommandees = 2 * utilisateur.getPoids();
                                lipidesRecommandees = 1 * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case ENDOMORPHE:
                                proteinesRecommandees = 2.2f * utilisateur.getPoids();
                                lipidesRecommandees = 1 * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                        }
                        break;
                }
                break;
            case INTERMEDIAIRE:
            case AVANCE:
                switch (utilisateur.getObjectif()) {
                    case PERTE_POIDS:
                        caloriePourRégime = -1000;
                        switch (utilisateur.getMorphotype()) {
                            case ECTOMORPHE:
                                proteinesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.3f / 4;
                                lipidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.3f / 9;
                                glucidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.4f / 4;
                                break;
                            case MESOMORPHE:
                                proteinesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.3f / 4;
                                lipidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.3f / 9;
                                glucidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.4f / 4;
                                break;
                            case ENDOMORPHE:
                                proteinesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.4f / 4;
                                lipidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.35f / 9;
                                glucidesRecommandees = (utilisateur.calculMetabolismeBase() + caloriePourRégime) * 0.25f / 4;
                                break;
                        }
                        break;
                    case GAIN_MASSE:
                        caloriePourRégime = 300;
                        switch (utilisateur.getMorphotype()) {
                            case ECTOMORPHE:
                                proteinesRecommandees = 1.8f * utilisateur.getPoids();
                                lipidesRecommandees = 1.5f * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case MESOMORPHE:
                                proteinesRecommandees = 2 * utilisateur.getPoids();
                                lipidesRecommandees = 1.5f * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                            case ENDOMORPHE:
                                proteinesRecommandees = 2.2f * utilisateur.getPoids();
                                lipidesRecommandees = 2f * utilisateur.getPoids();
                                glucidesRecommandees = (caloriePourRégime + utilisateur.calculMetabolismeBase() - proteinesRecommandees * 4 - lipidesRecommandees * 9) / 4;
                                break;
                        }
                        break;
                }
                break;
        }
        
    }
    
    
    @Override
    public String toString() {
        return "Regime{" + "idUtilisateur='" + idUtilisateur + '\'' + ", caloriesRecommandees=" + caloriesRecommandees + ", glucidesRecommandees=" + glucidesRecommandees + ", lipidesRecommandees=" + lipidesRecommandees + ", proteinesRecommandees=" + proteinesRecommandees + ", caloriePourRégime=" + caloriePourRégime + '}';
    }
}
