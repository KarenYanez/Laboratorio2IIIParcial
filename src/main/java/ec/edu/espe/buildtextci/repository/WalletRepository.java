package ec.edu.espe.buildtextci.repository;

import ec.edu.espe.buildtextci.model.Wallet;

import java.util.Optional;

public interface WalletRepository {

    Wallet save(Wallet wallet);
    Optional<Wallet> findbyId(String id);

    Boolean exitsByOwnerEmail(String ownerEmail);

}
