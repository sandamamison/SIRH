package SI.RH.entity;

import java.util.Date;

public class PropositionEmbauche {
    private int id;
    private int candidatureId;
    private int statutId;
    private Date dateEnvoi;
    private Date dateReponse;
    private Date dateExpiration;
    private Date created_at;
    private Date updated_at;

    public PropositionEmbauche(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCandidatureId() {
        return candidatureId;
    }

    public void setCandidatureId(int candidatureId) {
        this.candidatureId = candidatureId;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Date dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Date getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(Date dateReponse) {
        this.dateReponse = dateReponse;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
