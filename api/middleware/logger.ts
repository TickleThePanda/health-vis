import { Request, Response, NextFunction } from "express";

export function logger() {
  return (req: Request, _: Response, next: NextFunction) => {
    console.log(req.method, req.url);
    next();
  };
}
