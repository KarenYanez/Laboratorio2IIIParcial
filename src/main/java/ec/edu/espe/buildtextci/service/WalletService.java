package ec.edu.espe.buildtextci.service;

import ec.edu.espe.buildtextci.dto.WalletResponse;
import ec.edu.espe.buildtextci.model.Wallet;
import ec.edu.espe.buildtextci.repository.WalletRepository;

public class WalletService {

    private final WalletRepository walletRepository;
    private final RiskClient riskClient;

    public WalletService(WalletRepository walletRepository, RiskClient riskClient) {
        this.walletRepository = walletRepository;
        this.riskClient = riskClient;
    }

    // Crear una cuenta si cumple con las condiciones
    public WalletResponse createWallet(String ownerEmail, double initialBalance) {

        //  Validación: email nulo, vacío o mal formado
        if (ownerEmail == null || ownerEmail.trim().isEmpty()
                || !ownerEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("invalid email address");
        }

        //  Validación: balance negativo
        if (initialBalance < 0) {
            throw new IllegalArgumentException("initial balance canot be negative");
        }

        //  Regla de negocio: usuario bloqueado
        if (riskClient.isBloquead(ownerEmail)) {
            throw new IllegalStateException("userbloquead");
        }

        //  Regla de negocio: no duplicar billetera por email
        if (walletRepository.exitsByOwnerEmail(ownerEmail)) {
            throw new IllegalStateException("wallet already exits");
        }

        //  Crear la billetera
        Wallet wallet = new Wallet(ownerEmail, initialBalance);

        //  Guardar en repositorio
        walletRepository.save(wallet);

        //  Retornar respuesta
        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );
    }

    //depositar dinero
    public double deposit(String walletId, double amount) {

        //  Validación: monto negativo o cero
        if (amount <= 0) {
            throw new IllegalArgumentException("amount is not valid");
        }

        // Validación: wallet existe
        Wallet wallet = walletRepository.findbyId(walletId)
                .orElseThrow(() -> new IllegalStateException("wallet not found"));

        // Depositar
        wallet.deposit(amount);

        //  Guardar cambios
        walletRepository.save(wallet);

        //  Retornar balance actualizado
        return wallet.getBalance();
    }
}