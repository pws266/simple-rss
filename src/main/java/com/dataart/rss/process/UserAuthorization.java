package main.java.com.dataart.rss.process;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static main.java.com.dataart.rss.data.Reference.*;

/**
 * Authorization module for login procedure implementation.
 * It performs encryption (salt + hash) and verification of user password
 *
 * @author Sergey 'Manual Brakes' Sokhnyshev
 * Created by newbie on 25.09.17.
 */
public class UserAuthorization {
    private SecureRandom saltGenerator = new SecureRandom();        // salt sequence generator

    /**
     * Encrypts specified message using "salt + hash" algorithm
     *
     * @param msg - message for encryption
     * @param saltSequence - external generated "salt" sequence
     * @return hash representation of specified message as bytes array
     * @throws NoSuchAlgorithmException - if hash encryption algorithm isn't found
     */
    private byte[] getMessageHash(String msg, byte[] saltSequence) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM_NAME);

        // resetting and initializing hash generator with salt
        digest.reset();
        digest.update(saltSequence);

        return digest.digest(msg.getBytes());
    }

    /**
     * Embeds "salt"-sequence chunks into specified hash sequence
     *
     * @param hash - message hash representation
     * @param salt - "salt"-sequence used for getting hash
     * @return combined sequence as bytes array
     */
    private byte[] combineSequences(byte[] hash, byte[] salt) {
        byte[] combined = new byte[hash.length + salt.length];

        int hashChunkSz = hash.length/(SALT_CHUNKS_NUMBER + 1);
        int chunkSz = hashChunkSz + SALT_CHUNK_SZ;

        for (int i = 0; i < SALT_CHUNKS_NUMBER; ++i) {
            System.arraycopy(hash, i*hashChunkSz, combined, i*chunkSz, hashChunkSz);
            System.arraycopy(salt, i*SALT_CHUNK_SZ, combined, i*chunkSz + hashChunkSz, SALT_CHUNK_SZ);
        }

        int hashCopiedSz = SALT_CHUNKS_NUMBER*hashChunkSz;
        System.arraycopy(hash, hashCopiedSz, combined, SALT_CHUNKS_NUMBER*chunkSz,
                         hash.length - hashCopiedSz);

        return combined;
    }

    /**
     * Transforms bytes array to string with hex data representation. It is analogue of "printHexBinary" method
     * of "DatatypeConverter" class.
     * Hex string is 2 times longer than source bytes array.
     *
     * @param byteHash - source bytes array
     * @return string of hex data represented source bytes array
     */
    private String byteToHex(byte[] byteHash) {
        StringBuilder hexCombiner = new StringBuilder();

        for (byte x : byteHash) {
            // 0xFF - mask for byte
            // adding 0x100 and getting substring(1) for guaranted cutting leading 24 digits in integer
            hexCombiner.append(Integer.toString((x & 0xFF) + 0x100, 16).substring(1));
        }

        return hexCombiner.toString();
    }

    /**
     * Transforms string with hex data representation to bytes array. It is analogue of "parseHexBinary" method
     * of "DatatypeConverter" class.
     * Hex string is 2 times longer than source bytes array.
     *
     * @param hexHash - string of hex data source bytes array
     * @return bytes array obtained from source string
     */
    private byte[] hexToByte(String hexHash) {
        int hexSz = hexHash.length();
        byte[] byteHash = new byte[hexSz/2];

        for (int i = 0; i < hexSz; i += 2) {
            byteHash[i/2] = (byte)((Character.digit(hexHash.charAt(i), 16) << 4) +
                                   Character.digit(hexHash.charAt(i + 1), 16));
        }

        return byteHash;
    }

    /**
     * Extracts "salt"-sequence from specified general combined hash for user password verification purpose.
     *
     * @param hexHash - hex data string representing combined user password hash and "salt"-sequence chunks
     * @return extracted "salt"-sequence as bytes array
     */
    private byte[] extractSalt(String hexHash) {
        // getting whole mixed hash as byte array
        byte[] byteHash = hexToByte(hexHash);

        // reading salt from combined hash
        int hashChunkSz = (byteHash.length - SALT_SEQUENCE_SZ)/(SALT_CHUNKS_NUMBER + 1);
        int chunkSz = hashChunkSz + SALT_CHUNK_SZ;

        byte[] saltFromHash = new byte[SALT_SEQUENCE_SZ];

        for (int i = 0; i < SALT_CHUNKS_NUMBER; ++i) {
            System.arraycopy(byteHash, hashChunkSz + i*chunkSz, saltFromHash, i*SALT_CHUNK_SZ, SALT_CHUNK_SZ);
        }

        return saltFromHash;
    }

    /**
     * Encrypts user password using specified "salt"-sequence
     *
     * @param password - user password
     * @param salt - "salt"-sequence as bytes array
     * @return general combined hash as string with hex data
     * @throws NoSuchAlgorithmException - if hash encryption algorithm isn't found
     */
    private String encryptPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        // generating message hash
        byte[] msgHash = getMessageHash(password, salt);

        // combining hashes in special way
        byte[] combinedHash = combineSequences(msgHash, salt);

        return byteToHex(combinedHash);
    }

    /**
     * Encrypts user password with internal generated "salt"-sequence
     *
     * @param password - user password
     * @return general combined hash as string with hex data
     * @throws NoSuchAlgorithmException - if hash encryption algorithm isn't found
     */
    public String encryptPassword(String password) throws NoSuchAlgorithmException {
        // generating "salt" sequence for password encryption
        byte[] salt = new byte[SALT_SEQUENCE_SZ];
        saltGenerator.nextBytes(salt);

        return encryptPassword(password, salt);
    }

    /**
     * Verifies if user password corresponds to given generalized hash
     *
     * @param password - user password for verification
     * @param hash - known generalized hash (e.g. from database corresponding to user login)
     * @return "true" if user password matches to the hash, "false" - otherwise
     * @throws NoSuchAlgorithmException - if hash encryption algorithm isn't found
     */
    public boolean isCorrectPassword(String password, String hash) throws NoSuchAlgorithmException {
        // extracting "salt" sequence from predefined hash
        byte[] salt = extractSalt(hash);

        // encrypting specified password with the same "salt" sequence as predefined hash
        String passwordHash = encryptPassword(password, salt);

        return hash.equals(passwordHash);
    }
}
