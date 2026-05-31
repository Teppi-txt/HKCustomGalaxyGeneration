"use strict";

export class RNG {
  private seed: bigint;
  readonly MULTIPLIER = 0x5DEECE66Dn;
  readonly MASK = (1n << 48n) - 1n;


  constructor() {
    this.seed = (0n ^ this.MULTIPLIER) & this.MASK;
  }

  setSeed(seed: number) {
    this.seed = (BigInt(seed) ^ this.MULTIPLIER) & this.MASK;
  }


  protected next(bits: number): number {
    this.seed = (this.seed * this.MULTIPLIER + 0xBn) & this.MASK;
    return Number(this.seed >> (48n - BigInt(bits)));
  }

  nextInt(bound: number): number {
    if (bound <= 0) {
      throw new Error("bound must be positive");
    }

    if ((bound & -bound) === bound) {
      return Number((BigInt(bound) * BigInt(this.next(31))) >> 31n);
    }

    let bits: number;
    let val: number;

    do {
      bits = this.next(31);
      val = bits % bound;
    } while (bits - val + (bound - 1) < 0);

    return val;
  }

  nextDouble(): number {
    // this is not the current/best implementation of nextDouble, but the Java app runs on Java 8 which uses this formula
    // keeping it for parity
    return ((this.next(27) * 2 ** 27) + this.next(27)) / 2 ** 54;
  }
}
