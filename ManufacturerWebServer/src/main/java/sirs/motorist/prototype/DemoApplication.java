package sirs.motorist.prototype;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sirs.motorist.prototype.lib.util.SecurityUtil;
import sirs.motorist.prototype.model.entity.Firmware;
import sirs.motorist.prototype.model.entity.Mechanic;
import sirs.motorist.prototype.repository.FirmwareRepository;
import sirs.motorist.prototype.repository.MechanicRepository;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Autowired
	MechanicRepository mechanicRepository;

	@Autowired
	FirmwareRepository firmwareRepository;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		PublicKey publicKey1 = SecurityUtil.loadPublicKeyFromFile("src/main/java/sirs/motorist/prototype/mockKeys/alice.pubkey");
		PublicKey publicKey2 = SecurityUtil.loadPublicKeyFromFile("src/main/java/sirs/motorist/prototype/mockKeys/bob.pubkey");

		PrivateKey privateKey1 = SecurityUtil.loadPrivateKey("src/main/java/sirs/motorist/prototype/mockKeys/alice.privkey");
		PrivateKey privateKey2 = SecurityUtil.loadPrivateKey("src/main/java/sirs/motorist/prototype/mockKeys/bob.privkey");

		//System.out.println("Signature for mechanic alice: " + SecurityUtil.signData(("chassis1").getBytes(), privateKey1));

		Mechanic mechanic1 = new Mechanic("1", Base64.getEncoder().encodeToString(publicKey1.getEncoded()));
		Mechanic mechanic2 = new Mechanic("2", Base64.getEncoder().encodeToString(publicKey2.getEncoded()));

		Firmware firmware1 = new Firmware("Tesla 1", 1);
		Firmware firmware2 = new Firmware("Tesla 2", 2);

		/*firmwareRepository.save(firmware1);
		firmwareRepository.save(firmware2);

		mechanicRepository.save(mechanic1);
		mechanicRepository.save(mechanic2);*/
		}

}
