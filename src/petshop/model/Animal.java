package petshop.model;

/**
 * Representa um animal de estimacao vinculado a um cliente.
 */
public class Animal {

    private int idAnimal;
    private String nome;
    private String especie;
    private String raca;
    private int idCliente; // chave estrangeira para Cliente

    public Animal() {
    }

    public Animal(int idAnimal, String nome, String especie, String raca, int idCliente) {
        this.idAnimal = idAnimal;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idCliente = idCliente;
    }

    public Animal(String nome, String especie, String raca, int idCliente) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idCliente = idCliente;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return "Animal{" + "idAnimal=" + idAnimal + ", nome=" + nome
                + ", especie=" + especie + ", raca=" + raca
                + ", idCliente=" + idCliente + '}';
    }
}
