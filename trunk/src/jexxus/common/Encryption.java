package jexxus.common;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

	private static final String algorithm = "AES";

	public interface Algorithm {
		public byte[] encrypt(byte[] data);

		public byte[] decrpyt(byte[] data);
	}

	/*
	public static void main(String[] args) throws Exception {
		int maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
		System.out.println(maxKeyLen);
	}*/

	public static final Algorithm createEncryptionAlgorithm(Connection connection) {
		try {
			KeyPair keypair = generateDHKeyPair();

			// Get the generated public and private keys
			PrivateKey privateKey = keypair.getPrivate();
			PublicKey publicKey = keypair.getPublic();

			// Send the public key bytes to the other party...
			byte[] publicKeyBytes = publicKey.getEncoded();
			connection.sendTCP(publicKeyBytes);

			// Retrieve the public key bytes of the other party
			publicKeyBytes = connection.readTCP();

			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFact = KeyFactory.getInstance("DH");
			publicKey = keyFact.generatePublic(x509KeySpec);

			// Prepare to generate the secret key with the private key and public key of the other party
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(privateKey);
			ka.doPhase(publicKey, true);

			// Generate the secret key
			final byte[] sharedSecret = ka.generateSecret();
			final SecretKey secretKey = new SecretKeySpec(sharedSecret, 0, 16, algorithm);

			return new AESAlgorithm(secretKey);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static KeyPair generateDHKeyPair() throws Exception {
		final BigInteger p = new BigInteger(
				"f460d489678f7ec903293517e9193fd156c821b3e2b027c644eb96aedc85a54c971468cea07df15e9ecda0e2ca062161add38b9aa8aefcbd7ac18cd05a6bfb1147aaa516a6df694ee2cb5164607c618df7c65e75e274ff49632c34ce18da534ee32cfc42279e0f4c29101e89033130058d7f77744dddaca541094f19c394d485",
				16);
		final BigInteger g = new BigInteger(
				"9ce2e29b2be0ebfd7b3c58cfb0ee4e9004e65367c069f358effaf2a8e334891d20ff158111f54b50244d682b720f964c4d6234079d480fcc2ce66e0fa3edeb642b0700cd62c4c02a483c92d2361e41a23706332bd3a8aaed07fe53bba376cefbce12fa46265ad5ea5210a3d96f5260f7b6f29588f61a4798e40bdc75bbb2b457",
				16);

		final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
		final DHParameterSpec dhSpec = new DHParameterSpec(p, g, 1023);
		keyGen.initialize(dhSpec);
		return keyGen.generateKeyPair();
	}

	private static class AESAlgorithm implements Algorithm {

		private final Cipher ecipher;
		private final Cipher dcipher;

		public AESAlgorithm(SecretKey key) throws Exception {
			ecipher = Cipher.getInstance(algorithm);
			dcipher = Cipher.getInstance(algorithm);
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			dcipher.init(Cipher.DECRYPT_MODE, key);
		}

		@Override
		public byte[] encrypt(byte[] data) {
			try {
				return ecipher.doFinal(data);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public byte[] decrpyt(byte[] data) {
			try {
				return dcipher.doFinal(data);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

}
