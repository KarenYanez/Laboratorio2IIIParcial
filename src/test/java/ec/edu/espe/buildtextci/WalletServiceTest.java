package ec.edu.espe.buildtextci;

import ec.edu.espe.buildtextci.dto.WalletResponse;
import ec.edu.espe.buildtextci.model.Wallet;
import ec.edu.espe.buildtextci.repository.WalletRepository;
import ec.edu.espe.buildtextci.service.RiskClient;
import ec.edu.espe.buildtextci.service.WalletService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WalletServiceTest {

    private WalletRepository walletRepository;
    private RiskClient riskClient;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        riskClient = mock(RiskClient.class);
        walletService = new WalletService(walletRepository, riskClient);
    }

    @Test
    void createWallet_validData_shouldSaveAndReturnResponse() {
        // =========================
        // ARRANGE
        // =========================
        String email = "jsmena5@espe.edu.ec";
        double initialBalance = 100.0;

        // Wallet NO existe previamente
        when(walletRepository.exitsByOwnerEmail(email)).thenReturn(false);

        // Simular guardado
        when(walletRepository.save(ArgumentMatchers.any(Wallet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        WalletResponse response = walletService.createWallet(email, initialBalance);

        // ASSERT
        assertNotNull(response);
        assertNotNull(response.getWalletId());
        assertEquals(initialBalance, response.getBalance());

        // VERIFY
        verify(riskClient, times(1)).isBloquead(email);
        verify(walletRepository, times(1)).exitsByOwnerEmail(email);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void createWallet_invalidEmail_shouldThrowAndNotCallDependencies() {
        // ARRANGE
        String invalidEmail = "jsmena5@espe;edu.ec"; // email mal escrito
        double initialBalance = 1000.0;

        // ACT + ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> walletService.createWallet(invalidEmail, initialBalance)
        );

        assertEquals("invalid email address", exception.getMessage());


        // VERIFY (no se llaman dependencias)
        verifyNoInteractions(riskClient);
        verifyNoInteractions(walletRepository);
    }

    @Test
    void deposit_walletNotFound_shouldThrow() {
        // ARRANGE
        String walletId = "nonexistent-wallet-id";
        double amount = 500.0;

        // Simular que la wallet NO existe
        when(walletRepository.findbyId(walletId)).thenReturn(java.util.Optional.empty());


        // ACT + ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> walletService.deposit(walletId, amount)
        );

        assertEquals("wallet not found", exception.getMessage());

        // VERIFY (no se guarda nada)
        verify(walletRepository, times(1)).findbyId(walletId);
        verify(walletRepository, never()).save(any());
    }


    @Test
    void deposit_shouldUpdateBalanceAndSave_usingCaptor() {
        // ARRANGE
        String walletId = "wallet-123";
        String ownerEmail = "jsmena5@espe.edu.ec";
        double initialBalance = 100.0;
        double depositAmount = 500.0;
        // Crear nueva wallet
        Wallet wallet = new Wallet(ownerEmail, initialBalance);
        // Simular que la wallet existe en el repositorio
        when(walletRepository.findbyId(walletId)).thenReturn(Optional.of(wallet));
        // Captor para capturar la wallet que se guarda
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        // ACT
        double updatedBalance = walletService.deposit(walletId, depositAmount);
        // ASSERT
        // Balance retornado correcto
        assertEquals(initialBalance + depositAmount, updatedBalance);

        // Verificar que save fue llamado con la wallet actualizada
        verify(walletRepository).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(initialBalance + depositAmount, savedWallet.getBalance());
        assertEquals(ownerEmail, savedWallet.getOwnerEmail());
    }

}