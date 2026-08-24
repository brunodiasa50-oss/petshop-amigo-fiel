package petshop.model;

import java.time.LocalDateTime;

/**
 * Representa o agendamento de um servico (banho, tosa, consulta, etc.)
 * para um animal cadastrado.
 */
public class Agendamento {

    private int idAgendamento;
    private int idAnimal; // chave estrangeira para Animal
    private String servico;
    private LocalDateTime dataHora;
    private String status; // Ex.: AGENDADO, CONCLUIDO, CANCELADO

    public Agendamento() {
    }

    public Agendamento(int idAgendamento, int idAnimal, String servico,
            LocalDateTime dataHora, String status) {
        this.idAgendamento = idAgendamento;
        this.idAnimal = idAnimal;
        this.servico = servico;
        this.dataHora = dataHora;
        this.status = status;
    }

    public Agendamento(int idAnimal, String servico, LocalDateTime dataHora, String status) {
        this.idAnimal = idAnimal;
        this.servico = servico;
        this.dataHora = dataHora;
        this.status = status;
    }

    public int getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(int idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Agendamento{" + "idAgendamento=" + idAgendamento
                + ", idAnimal=" + idAnimal + ", servico=" + servico
                + ", dataHora=" + dataHora + ", status=" + status + '}';
    }
}
