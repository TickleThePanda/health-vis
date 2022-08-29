import { NextFunction, Request, Response } from "express";

export function noCache() {
  return (_: Request, res: Response, next: NextFunction) => {
    res.set("Cache-Control", "no-cache");
    next();
  };
}
